curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=4001&endingIndex=5000' > 4001_5000.txt
git add --all
git commit -am "[seeder] manga: 4001 - 5000"
git push origin HEAD:4001
