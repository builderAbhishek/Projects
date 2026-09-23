package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"

	gtranslate "github.com/gilang-as/google-translate"
)

type TranslationRequest struct {
	Text string `json:"text"`
	To   string `json:"to"`
}

func translateHandler(w http.ResponseWriter, r *http.Request) {
	var req TranslationRequest
	err := json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	value := gtranslate.Translate{
		Text: req.Text,
		To:   req.To,
	}

	translated, err := gtranslate.Translator(value)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(translated)
}

func main() {
	http.HandleFunc("/translate", translateHandler)
	fmt.Println("Translation service started on port 8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}