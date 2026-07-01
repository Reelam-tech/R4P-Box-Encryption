# R4P-Box Encryption Algorithm

A hybrid encryption algorithm built in Java combining 
4-bit rotation, P-Box permutation, and SHA-256 hashing.

## How It Works
1. Convert each character to 8-bit binary
2. Apply 4-bit left rotation (swap first and last 4 bits)
3. Apply Straight P-Box permutation to rearrange bits
4. Generate SHA-256 hash of ciphertext for integrity check
5. Decryption reverses the process after verifying the hash

## Features
- Lightweight bit-level encryption
- Tamper detection via SHA-256
- Full encrypt/decrypt pipeline

## Tools
Java — NetBeans IDE

## Course
IT311 — Princess Nourah bint Abdulrahman University
