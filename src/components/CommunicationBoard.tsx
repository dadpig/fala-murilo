import { useState, useEffect, useCallback } from 'react'
import { CARDS, PHOTO_STORAGE_PREFIX } from '../data/cards'
import { Card } from './Card'
import { LanguageSelector } from './LanguageSelector'
import { useSpeech } from '../hooks/useSpeech'
import type { Language } from '../types'

const LANG_STORAGE_KEY = 'fala-murilo:language'

function loadLanguage(): Language {
  const saved = localStorage.getItem(LANG_STORAGE_KEY)
  if (saved === 'en-US' || saved === 'es-ES' || saved === 'pt-BR') return saved
  const nav = navigator.language
  if (nav.startsWith('pt')) return 'pt-BR'
  if (nav.startsWith('es')) return 'es-ES'
  return 'en-US'
}

export function CommunicationBoard() {
  const [language, setLanguage] = useState<Language>(loadLanguage)
  // Increments when a photo changes, forcing Card to re-read localStorage
  const [storageVersion, setStorageVersion] = useState(0)
  const { speak } = useSpeech(language)

  useEffect(() => {
    localStorage.setItem(LANG_STORAGE_KEY, language)
  }, [language])

  useEffect(() => {
    function handleStorage(e: StorageEvent) {
      if (e.key?.startsWith(PHOTO_STORAGE_PREFIX)) {
        setStorageVersion((v) => v + 1)
      }
    }
    window.addEventListener('storage', handleStorage)
    return () => window.removeEventListener('storage', handleStorage)
  }, [])

  const handleSpeak = useCallback(
    (text: string) => speak(text),
    [speak]
  )

  return (
    <div className="board">
      <header className="board__header">
        <h1 className="board__title">Fala, Murilo! 🌟</h1>
        <LanguageSelector language={language} onChange={setLanguage} />
      </header>

      <main className="board__grid" aria-label="Communication board">
        {CARDS.map((card) => (
          <Card
            key={`${card.id}-${storageVersion}`}
            card={card}
            language={language}
            onSpeak={handleSpeak}
          />
        ))}
      </main>
    </div>
  )
}
