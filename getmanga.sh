curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=1000&endingIndex=1200' > 1000_1200.txt
git add --all
git commit -am "[seeder] manga: 1000 - 1200"
git push origin HEAD:1000
