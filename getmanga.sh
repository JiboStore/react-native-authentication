curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=500&endingIndex=1001' > 500_1001.txt
git add --all
git commit -am "[seeder] manga: 500 - 1001"
git push origin HEAD:500
