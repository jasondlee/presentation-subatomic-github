
function build() {
    npx asciidoctor-revealjs slides.ad
}

build
inotifywait -e close_write,moved_to,create -m . |
while read -r directory events filename; do
#  if [ "$filename" = "index.adoc" ]; then
    build
#  fi
done
