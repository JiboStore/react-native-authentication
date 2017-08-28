curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=5001&endingIndex=6000' > 5001_6000.txt
git add --all
git commit -am "[seeder] manga: 5001 - 6000"
git push origin HEAD:5001
