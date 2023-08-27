import sys
from transformers import M2M100ForConditionalGeneration, M2M100Tokenizer


def translate_text(source_text, source_language, target_language):
    model = M2M100ForConditionalGeneration.from_pretrained("alirezamsh/small100")
    tokenizer = M2M100Tokenizer.from_pretrained("alirezamsh/small100", tgt_lang=target_language)

    inputs = tokenizer(source_text, return_tensors="pt")

    max_new_tokens = 100
    translated_ids = model.generate(**inputs, max_length=max_new_tokens)
    translated_text = tokenizer.decode(translated_ids[0], skip_special_tokens=True)

    translated_text = translated_text.replace('\U0001f642', '')

    return translated_text


if __name__ == "__main__":
    source_text = sys.argv[1]
    source_language = sys.argv[2]
    target_language = sys.argv[3]

    translated_text = translate_text(source_text, source_language, target_language)
    print(translated_text)
