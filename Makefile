.PHONY: deploy shadow clerk test garden-deploy deploy-js watch deploy-clerk

watch:
	npx shadow-cljs watch chipper-chaps-chateau portfolio

test:
	clj -M:dev -m kaocha.runner

shadow:
	npx shadow-cljs release chipper-chaps-chateau

clerk:
	clj -X:dev nextjournal.clerk/build! '{:paths ["notebooks/*"] :out-path "public/clerk"}'

garden-deploy:
	garden deploy

deploy-js:
	echo "put target/chipper-chaps-chateau/public/js/main.js public/js/main.js" | garden sftp

deploy-clerk:
	echo "put public/clerk/index.edn public/clerk/index.edn" | garden sftp
	echo "put public/clerk/index.html public/clerk/index.html" | garden sftp

deploy: test shadow garden-deploy deploy-js
