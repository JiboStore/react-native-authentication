curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=200&endingIndex=300' > 200_300.txt
git add --all
git commit -am "[seeder] manga: 200 - 300"
git push origin 200:200
