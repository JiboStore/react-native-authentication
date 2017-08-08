curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=2000&endingIndex=3000' > 2000_3000.txt
git add --all
git commit -am "[seeder] manga: 2000 - 3000"
git push origin HEAD:2000
