import { useCallback, useRef } from 'react'
import type { Language } from '../types'

export function useSpeech(language: Language) {
  const utteranceRef = useRef<SpeechSynthesisUtterance | null>(null)

  const speak = useCallback(
    (text: string) => {
      if (!('speechSynthesis' in window)) {
        console.warn('Web Speech API not supported in this browser.')
        return
      }

      window.speechSynthesis.cancel()

      const utterance = new SpeechSynthesisUtterance(text)
      utterance.lang = language
      utterance.rate = 0.85
      utterance.pitch = 1.1
      utterance.volume = 1

      const voices = window.speechSynthesis.getVoices()
      const langPrefix = language.slice(0, 2)
      const match =
        voices.find((v) => v.lang === language) ||
        voices.find((v) => v.lang.startsWith(langPrefix))
      if (match) utterance.voice = match

      utteranceRef.current = utterance
      window.speechSynthesis.speak(utterance)
    },
    [language]
  )

  return { speak }
}
