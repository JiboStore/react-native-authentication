curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=501&endingIndex=1001' > 501_1001.txt
git add --all
git commit -am "[seeder] manga: 501 - 1001"
git push origin HEAD:501
