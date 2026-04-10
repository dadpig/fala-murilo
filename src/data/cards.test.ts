import { describe, it, expect } from 'vitest'
import { CARDS, PHOTO_STORAGE_PREFIX } from './cards'

describe('CARDS', () => {
  it('has 9 cards', () => {
    expect(CARDS).toHaveLength(9)
  })

  it('every card has a unique id', () => {
    const ids = CARDS.map((c) => c.id)
    expect(new Set(ids).size).toBe(ids.length)
  })

  it('every card provides labels for all three languages', () => {
    const languages = ['en-US', 'es-ES', 'pt-BR'] as const
    for (const card of CARDS) {
      for (const lang of languages) {
        expect(card.labels[lang]).toBeTruthy()
      }
    }
  })

  it('every card has a non-empty emoji', () => {
    for (const card of CARDS) {
      expect(card.emoji.trim()).not.toBe('')
    }
  })

  it('every card has a color string starting with #', () => {
    for (const card of CARDS) {
      expect(card.color).toMatch(/^#/)
    }
  })
})

describe('PHOTO_STORAGE_PREFIX', () => {
  it('is a non-empty string', () => {
    expect(typeof PHOTO_STORAGE_PREFIX).toBe('string')
    expect(PHOTO_STORAGE_PREFIX.length).toBeGreaterThan(0)
  })
})
