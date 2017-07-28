curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=104&endingIndex=200' > 104_200.txt
git add --all
git commit -am "[seeder] manga: 104 - 200"
git push origin HEAD:104
