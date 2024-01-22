inotifywait -e close_write,moved_to,create -m . |
while read -r directory events filename; do
#  if [ "$filename" = "index.adoc" ]; then
    npx asciidoctor-revealjs slides.ad
    echo Built
#  fi
done
