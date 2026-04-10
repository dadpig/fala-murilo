import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { CommunicationBoard } from './CommunicationBoard'
import { CARDS } from '../data/cards'

const mockCancel = vi.fn()
const mockSpeak = vi.fn()

beforeEach(() => {
  vi.clearAllMocks()
  localStorage.clear()
  Object.defineProperty(window, 'speechSynthesis', {
    value: { cancel: mockCancel, speak: mockSpeak, getVoices: () => [] },
    writable: true,
    configurable: true,
  })
})

describe('CommunicationBoard', () => {
  it('renders the app title', () => {
    render(<CommunicationBoard />)
    expect(screen.getByText('Fala, Murilo! 🌟')).toBeInTheDocument()
  })

  it('renders all cards', () => {
    render(<CommunicationBoard />)
    expect(screen.getAllByRole('button', { name: /tap to speak/i })).toHaveLength(CARDS.length)
  })

  it('renders the language selector', () => {
    render(<CommunicationBoard />)
    expect(screen.getByRole('group', { name: 'Select language' })).toBeInTheDocument()
  })

  it('speaks the card label when a card is clicked', async () => {
    render(<CommunicationBoard />)
    // Default language is en-US (navigator.language is not set in jsdom)
    const eatBtn = screen.getByRole('button', { name: /Eat, tap to speak/i })
    await userEvent.click(eatBtn)
    expect(mockSpeak).toHaveBeenCalledOnce()
    const utterance = mockSpeak.mock.calls[0][0] as SpeechSynthesisUtterance
    expect(utterance.text).toBe('Eat')
  })

  it('switches language and re-renders card labels', async () => {
    render(<CommunicationBoard />)
    await userEvent.click(screen.getByRole('button', { name: 'Português' }))
    expect(screen.getByRole('button', { name: /Comer, tap to speak/i })).toBeInTheDocument()
  })

  it('persists the selected language to localStorage', async () => {
    render(<CommunicationBoard />)
    await userEvent.click(screen.getByRole('button', { name: 'Español' }))
    expect(localStorage.getItem('fala-murilo:language')).toBe('es-ES')
  })
})
