curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=0&endingIndex=99' > 0_99.txt
git add --all
git commit -am "[seeder] manga: 0 - 99"
git push origin seeder:seeder
