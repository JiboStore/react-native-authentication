curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=1201&endingIndex=2000' > 1201_2000.txt
git add --all
git commit -am "[seeder] manga: 1201 - 2000"
git push origin HEAD:1201
