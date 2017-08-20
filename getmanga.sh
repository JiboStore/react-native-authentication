curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=2443&endingIndex=3000' > 2443_3000.txt
git add --all
git commit -am "[seeder] manga: 2443 - 3000"
git push origin HEAD:2000
