/**
 * 版权所有 (c) 2017， 吕绪海. 保留所有权利
 * Copyright (c) 2017, Hai Thomson. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.kernel.cache;

import source.kernel.config.GlobalConfig;
import source.kernel.serialization.oracle.JavaSerialization;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * File类不会加锁
 * 读写文件的类都会自动加锁
 * @author Hai Thomson
 */
public class FileCache {

	// static 只有在第一次运行时才被加载而不是项目加载的时候!
	static {
		File file = new File(GlobalConfig.FILE_CACHE_DIRECTORY);
		if (file.isFile()) {
			throw new RuntimeException(GlobalConfig.FILE_CACHE_DIRECTORY + " is a file! Not is a Directory");
		}

		if (!file.exists()) {
			file.mkdirs();
			System.out.println(GlobalConfig.FILE_CACHE_DIRECTORY + " is auto create");
		}

		if (!file.canWrite()) {
			throw new RuntimeException(GlobalConfig.FILE_CACHE_DIRECTORY + " is not writable");
		}
	}

	public static boolean putCache(String cachename, byte[] data, long expiration) throws CacheException {
		boolean fileIsExists = true;

		if (data == null) {
			throw new CacheException("Cache data is null. Can't save null.");
		}

		if (cachename == null || cachename.equals("")) {
			throw new CacheException("Cache name is null or \"\". Can't save null.");
		}

		String cacheDirectoryPath = GlobalConfig.FILE_CACHE_DIRECTORY;
		File file = new File(cacheDirectoryPath + cachename);
		if (!file.exists()) {
			fileIsExists = false;
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new CacheException("Failed to create cache file. " + e.getMessage());
			}
		}

		if (!file.canWrite()) {
			throw new CacheException("Cache file [" + file.getAbsolutePath() + "] is not writable");
		}

		RandomAccessFile cacheNodeFile = null;
		FileChannel cacheNodeFileChannel = null;
		try {
			cacheNodeFile = new RandomAccessFile(file, "rw");
			cacheNodeFileChannel = cacheNodeFile.getChannel();
			//FileLock lock = cacheNodeFileChannel.lock();

			ByteBuffer status = ByteBuffer.allocate(1);
			int redLen = cacheNodeFileChannel.read(status);
			byte cacheNodeFileStatus = 1;
			if (redLen > 0) {
				status.flip();
				cacheNodeFileStatus = status.get();
			}
			if (fileIsExists && cacheNodeFileStatus == 0) {
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
				// 校验缓存文件是否已损坏
				// 文件状态为正在写入，但已五分钟内没有修改.则认为上次写入时发生错误.
				if (file.lastModified() != 0 && System.currentTimeMillis() - file.lastModified() > 5L * 60L * 1000L) {
					FileCache.delete(cachename);
					throw new CacheException("Last save error");
				}
				System.out.println("其它线程正在写入缓存.放弃这次写入！");
			}

			// System.out.println("开始写入");

			cacheNodeFile.seek(0);
			status = ByteBuffer.allocate(1);
			status.put((byte)0);
			status.flip();
			cacheNodeFileChannel.write(status);

			ByteBuffer expirationBuffer = ByteBuffer.allocate(8);
			expirationBuffer.putLong(expiration + System.currentTimeMillis());
			expirationBuffer.flip();
			cacheNodeFileChannel.write(expirationBuffer);

			ByteBuffer dataBuffer = ByteBuffer.allocate(data.length);
			dataBuffer.put(data);
			dataBuffer.flip();
			cacheNodeFileChannel.write(dataBuffer);

			cacheNodeFile.seek(0);
			status = ByteBuffer.allocate(1);
			status.put((byte)1);
			status.flip();
			cacheNodeFileChannel.write(status);

			//lock.close();
			cacheNodeFileChannel.close();
			cacheNodeFile.close();

			//System.out.println("写入完成");

			return true;
		} catch (FileNotFoundException e) {
			throw new CacheException("FileCache-1 The cache file was deleted while writing the file. Incentives may be highly concurrent " + e.getMessage());
		} /*catch (BufferUnderflowException e) {
			try {
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
			} catch (IOException ue) {
				throw new CacheException("Error closing resource " + ue.getMessage());
			}
			FileCache.delete(cachename);
			throw new CacheException("file is null " + e.getMessage());
		}*/ catch (IOException e) {
			try {
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
			} catch (IOException ue) {
				throw new CacheException("Error closing resource " + ue.getMessage());
			}
			throw new CacheException("FileCache-2 The cache file was deleted while writing the file. Incentives may be highly concurrent " + e.getMessage());
		}
	}

	/**
	 * 数据长度不能大于Integer.MAX_VALUE.实际saveCache时也不会大于这个长度.以防外部更改.
	 * @param cachename
	 * @return
	 */
	public static byte[] loadCache(String cachename) throws CacheException {
		if (cachename == null || cachename.equals("")) {
			throw new CacheException("Cache name is null or \"\". Can't load from null.");
		}

		String cacheDirectoryPath = GlobalConfig.FILE_CACHE_DIRECTORY;
		File file = new File(cacheDirectoryPath + cachename);
		if (!file.exists() || file.isDirectory() || file.length() > Integer.MAX_VALUE - 9) {
			return null;
		}

		if (!file.canRead()) {
			throw new CacheException("FileCache File [" + file.getAbsolutePath() + "] can't read");
		}

		RandomAccessFile cacheNodeFile = null;
		FileChannel cacheNodeFileChannel = null;
		try {
			cacheNodeFile = new RandomAccessFile(file, "rw");
			cacheNodeFileChannel = cacheNodeFile.getChannel();
			//FileLock lock = cacheNodeFileChannel.lock(0, file.length(), true);

			//byte cacheNodeFileStatus = cacheNodeFile.readByte();
			//cacheNodeFile.seek(0);
			ByteBuffer status = ByteBuffer.allocate(1);
			int redLen = cacheNodeFileChannel.read(status);
			byte cacheNodeFileStatus = 1;
			if (redLen > 0) {
				status.flip();
				cacheNodeFileStatus = status.get();
			} else {
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
				System.out.println("其它线程正在写入缓存.放弃这次载入！");
				return null;
			}
			if (cacheNodeFileStatus == 0) {
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
				// 校验缓存文件是否已损坏
				// 文件状态为正在写入，但已五分钟内没有修改.则认为上次写入时发生错误.
				if (file.lastModified() != 0 && System.currentTimeMillis() - file.lastModified() > 5L * 60L * 1000L) {
					FileCache.delete(cachename);
					throw new CacheException("Last save error");
				}
				System.out.println("其它线程正在写入缓存.放弃这次载入！");
				return null;
			}

			//long cacheNodeExpiration = cacheNodeFile.readLong();
			//cacheNodeFile.seek(1);
			ByteBuffer expiration = ByteBuffer.allocate(8);
			cacheNodeFileChannel.read(expiration);
			expiration.flip();
			long cacheNodeExpiration = expiration.getLong();
			if (cacheNodeExpiration < System.currentTimeMillis()) {
				//lock.close();
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
				FileCache.delete(cachename);
				return null;
			}

			//byte[] cacheNodeData = new byte[(int)file.length() - 9];
			//int readLen = cacheNodeFile.read(cacheNodeData, 0, cacheNodeData.length);
			//cacheNodeFile.seek(8);
			ByteBuffer data = ByteBuffer.allocate((int)file.length());
			int readLen = cacheNodeFileChannel.read(data);
			data.flip();
			byte[] cacheNodeData = data.array();
			if (readLen > 0) {
				//lock.close();
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
				return cacheNodeData;
			} else {
				//lock.close();
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
				FileCache.delete(cachename);
				return null;
			}
		} catch (FileNotFoundException e) {
			throw new CacheException("FileCache-3 The cache file was deleted while reading the file. Incentives may be highly concurrent " + e.getMessage());
		} /*catch (BufferUnderflowException e) {
			try {
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
			} catch (IOException ue) {
				throw new CacheException("Error closing resource " + ue.getMessage());
			}
			FileCache.delete(cachename);
			throw new CacheException("file is null " + e.getMessage());
		}*/ catch (IOException e) {
			try {
				cacheNodeFileChannel.close();
				cacheNodeFile.close();
			} catch (IOException ue) {
				throw new CacheException("Error closing resource " + ue.getMessage());
			}
			FileCache.delete(cachename);
			throw new CacheException("An IO error occurred while reading the file. Incentives may be highly concurrent " + e.getMessage());
		}
	}

	public static boolean put(String cachename, Object object, long expiration) throws CacheException {
		// 基本类型的变量永远不会为null
		if (object == null) {
			throw new CacheException("Cache data is null. Can't save null.");
		}

		if (cachename == null || cachename.equals("")) {
			throw new CacheException("Cache name is null or \"\". Can't save null.");
		}

		try {
			byte[] cacheNodeData = JavaSerialization.serializableToBytes(object);
			return FileCache.putCache(cachename, cacheNodeData, expiration);
		} catch (IOException e) {
			throw new CacheException("IO error occurred during serialization " + e.getMessage());
		}
	}

	public static Object load(String cachename) throws CacheException {
		if (cachename == null || cachename.equals("")) {
			throw new CacheException("Cache name is null or \"\". Can't load from null.");
		}

		byte[] cacheNodeData = FileCache.loadCache(cachename);
		if (cacheNodeData == null) {
			return null;
		}

		try {
			return JavaSerialization.serializationFromBytes(cacheNodeData);
		} catch (ClassNotFoundException e) {
			throw new CacheException("Class not found occurred during serialization " + e.getMessage());
		} catch (IOException e) {
			FileCache.delete(cachename);
			throw new CacheException("Serialization failure " + e.getMessage());
		}
	}

	public static boolean delete(String cachename) throws CacheException {
		if (cachename == null || cachename.equals("")) {
			throw new CacheException("Cache name is null or \"\". Can't delete from null.");
		}

		String cacheDirectoryPath = GlobalConfig.FILE_CACHE_DIRECTORY;
		File file = new File(cacheDirectoryPath + cachename);
		if (!file.exists()) {
			return true;
		}

		file.delete();
		return true;
	}
}
