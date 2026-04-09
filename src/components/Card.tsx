import { useRef } from 'react'
import type { CardData, Language } from '../types'
import { PHOTO_STORAGE_PREFIX } from '../data/cards'
import './Card.css'

interface Props {
  card: CardData
  language: Language
  onSpeak: (text: string) => void
}

export function Card({ card, language, onSpeak }: Props) {
  const storageKey = `${PHOTO_STORAGE_PREFIX}${card.id}`
  const fileInputRef = useRef<HTMLInputElement>(null)
  const customPhoto = localStorage.getItem(storageKey)

  function handleCardClick() {
    onSpeak(card.labels[language])
  }

  function handlePhotoButtonClick(e: React.MouseEvent) {
    e.stopPropagation()
    fileInputRef.current?.click()
  }

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return

    const reader = new FileReader()
    reader.onload = (evt) => {
      const dataUrl = evt.target?.result as string
      localStorage.setItem(storageKey, dataUrl)
      // Notify CommunicationBoard to increment storageVersion
      window.dispatchEvent(new StorageEvent('storage', { key: storageKey, newValue: dataUrl }))
    }
    reader.readAsDataURL(file)
    e.target.value = ''
  }

  return (
    <div
      className="card"
      style={{ '--card-color': card.color } as React.CSSProperties}
      onClick={handleCardClick}
      role="button"
      tabIndex={0}
      aria-label={`${card.labels[language]}, tap to speak`}
      onKeyDown={(e) => (e.key === 'Enter' || e.key === ' ') && handleCardClick()}
    >
      <div className="card__media">
        {customPhoto ? (
          <img
            src={customPhoto}
            alt={card.labels[language]}
            className="card__photo"
            draggable={false}
          />
        ) : (
          <span className="card__emoji" aria-hidden="true">
            {card.emoji}
          </span>
        )}
      </div>

      <p className="card__label">{card.labels[language]}</p>

      <button
        className="card__photo-btn"
        onClick={handlePhotoButtonClick}
        aria-label={`Change photo for ${card.labels[language]}`}
        title="Change photo"
      >
        📷
      </button>

      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        capture="environment"
        className="card__file-input"
        onChange={handleFileChange}
        aria-hidden="true"
        tabIndex={-1}
      />
    </div>
  )
}
