import random

words = ["apple", "train", "candy", "music", "brain"]
word_to_guess = random.choice(words)
guessed_letters = []
max_attempts = 6
attempts = 0

while attempts < max_attempts:
    display_word = ""
    for letter in word_to_guess:
        if letter in guessed_letters:
            display_word += letter + " "
        else:
            display_word += "_ "
    print("\nWord: ", display_word.strip())

    if "_" not in display_word:
        print("🎉 Congratulations! You guessed the word.")
        break

    guess = input("Guess a letter: ").lower()

    if not guess.isalpha() or len(guess) != 1:
        print("⚠️ Enter only a single letter.")
        continue

    if guess in guessed_letters:
        print("🔁 You already guessed that letter.")
        continue

    guessed_letters.append(guess)

    if guess not in word_to_guess:
        attempts += 1
        print(f"❌ Incorrect! You have {max_attempts - attempts} attempts left.")

else:
    print(f"\n💀 Game over! The word was: {word_to_guess}")
