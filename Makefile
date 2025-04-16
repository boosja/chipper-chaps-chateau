.PHONY: deploy shadow clerk test garden-deploy deploy-js watch

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

deploy: test shadow clerk garden-deploy deploy-js
