DIR=$(pwd)

rm -rf src/main/resources/static/*

cd client/app-photo-client
ng build --prod

cd dist/app-photo-client
cp -rf * ../../../../src/main/resources/static

cd $DIR

exit 0

