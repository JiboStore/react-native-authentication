curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=273&endingIndex=500' > 273_500.txt
git add --all
git commit -am "[seeder] manga: 273 - 500"
git push origin HEAD:273
