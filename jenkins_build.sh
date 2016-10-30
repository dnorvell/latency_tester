#Set Environmental Variables
OUTPUT_PATH=/Volumes/Projects/mobile/Jenkins/${PROJECT_NAME}
BUILD_PATH=${OUTPUT_PATH}/${BUILD_NUMBER}-\(${GIT_COMMIT:0:7}\)
UNIT_TEST_PATH=${BUILD_PATH}/UnitTestReport/

mkdir ${OUTPUT_PATH} ${BUILD_PATH} ${UNIT_TEST_PATH}

#Generate build_info.txt
cat <<EOF >${BUILD_PATH}/build_info.txt
Jenkins Build Information for ${PROJECT_NAME}:

------------------ Build Information ------------------
Job Name:         ${JOB_NAME}
Build Number:     ${BUILD_NUMBER}
Build Server:     ${NODE_NAME}
Build Started At: ${BUILD_ID}.
Triggered By:     ${BUILD_CAUSE}
Built URL:        ${BUILD_URL}


------------------- Git Information -------------------
Commit ID: ${GIT_COMMIT}
Branch:    ${GIT_BRANCH}
URL:       ${GIT_URL}

------------------- Mobile Web URLs -------------------
Debug APK:        http://mobile.wolfgang.com/${PROJECT_NAME}/${BUILD_NUMBER}-(${GIT_COMMIT:0:7})/${PROJECT_NAME}-debug.apk
Unit Test Report: http://mobile.wolfgang.com/${PROJECT_NAME}/${BUILD_NUMBER}-(${GIT_COMMIT:0:7})/UnitTestReport/index.html
EOF

#Create local.properties
echo "sdk.dir=${ANDROID_SDK_DIR}\nndk.dir=${ANDROID_NDK_DIR}" > local.properties

#Build project using gradle
./gradlew clean
./gradlew clean assemble
./gradlew test

#Move appropriate files to output directories
mv app/build/outputs/apk/app-debug.apk ${BUILD_PATH}/${PROJECT_NAME}-debug.apk
mv app/build/reports/tests/* ${UNIT_TEST_PATH}

#Remove old builds (change keep to then number of builds you want to keep)
cd ${OUTPUT_PATH}
count=$(ls | wc -l)
keep=30
if [ $count -gt $keep ]
then
	ls -tr | head -n$((count-keep)) | xargs rm -rf
fi