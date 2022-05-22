#!/usr/bin/env bash

source="C:\soft\git\pf220191824\pf2-web\business-web-frontend-angular"
tardget="C:\soft\git\unistar4me\20.frontend\business-web-frontend-angular"

cp -af $source"\app" $tardget

cp -af $source'\bower_components' $tardget

cp -af $tardget'\src\webpack.start.js' $tardget'\app\scripts\'

rm -rf $tardget'\app\scripts\app.js'

xcopy /Y /E "C:\soft\git\pf220191824\pf2-web\business-web-frontend-angular\app" "C:\soft\git\unistar4me\20.frontend\business-web-frontend-angular\app"


