mysqldump -uroot -p123456 --database bubble --default-character-set=utf8 > bubble.sql
echo "手动进行格式转换GBK→UTF8"
@pause