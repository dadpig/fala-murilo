import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Card } from './Card'
import type { CardData } from '../types'

const card: CardData = {
  id: 'eat',
  emoji: '🍽️',
  labels: { 'en-US': 'Eat', 'es-ES': 'Comer', 'pt-BR': 'Comer' },
  color: '#FF6B6B',
}

beforeEach(() => {
  localStorage.clear()
})

describe('Card', () => {
  it('renders the emoji when no custom photo is stored', () => {
    render(<Card card={card} language="en-US" onSpeak={vi.fn()} />)
    expect(screen.getByText('🍽️')).toBeInTheDocument()
  })

  it('renders the label in the selected language', () => {
    render(<Card card={card} language="en-US" onSpeak={vi.fn()} />)
    expect(screen.getByText('Eat')).toBeInTheDocument()
  })

  it('renders the label in pt-BR', () => {
    render(<Card card={card} language="pt-BR" onSpeak={vi.fn()} />)
    expect(screen.getByText('Comer')).toBeInTheDocument()
  })

  it('calls onSpeak with the label text when clicked', async () => {
    const onSpeak = vi.fn()
    render(<Card card={card} language="en-US" onSpeak={onSpeak} />)
    await userEvent.click(screen.getByRole('button', { name: /Eat, tap to speak/i }))
    expect(onSpeak).toHaveBeenCalledOnce()
    expect(onSpeak).toHaveBeenCalledWith('Eat')
  })

  it('calls onSpeak when Enter key is pressed', async () => {
    const onSpeak = vi.fn()
    render(<Card card={card} language="en-US" onSpeak={onSpeak} />)
    const cardEl = screen.getByRole('button', { name: /Eat, tap to speak/i })
    cardEl.focus()
    await userEvent.keyboard('{Enter}')
    expect(onSpeak).toHaveBeenCalledOnce()
  })

  it('renders a custom photo from localStorage when present', () => {
    const dataUrl = 'data:image/png;base64,abc'
    localStorage.setItem('fala-murilo:photo:eat', dataUrl)
    render(<Card card={card} language="en-US" onSpeak={vi.fn()} />)
    const img = screen.getByRole('img', { name: 'Eat' })
    expect(img).toHaveAttribute('src', dataUrl)
  })

  it('has an accessible aria-label on the card', () => {
    render(<Card card={card} language="en-US" onSpeak={vi.fn()} />)
    expect(screen.getByRole('button', { name: 'Eat, tap to speak' })).toBeInTheDocument()
  })

  it('has a photo-change button with an accessible label', () => {
    render(<Card card={card} language="en-US" onSpeak={vi.fn()} />)
    expect(screen.getByRole('button', { name: 'Change photo for Eat' })).toBeInTheDocument()
  })
})
