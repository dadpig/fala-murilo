import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useSpeech } from './useSpeech'

const mockCancel = vi.fn()
const mockSpeak = vi.fn()
const mockGetVoices = vi.fn(() => [] as SpeechSynthesisVoice[])

function defineSpeechSynthesis() {
  Object.defineProperty(window, 'speechSynthesis', {
    value: { cancel: mockCancel, speak: mockSpeak, getVoices: mockGetVoices },
    writable: true,
    configurable: true,
  })
}

beforeEach(() => {
  vi.clearAllMocks()
  defineSpeechSynthesis()
})

afterEach(() => {
  Object.defineProperty(window, 'speechSynthesis', {
    value: { cancel: mockCancel, speak: mockSpeak, getVoices: mockGetVoices },
    writable: true,
    configurable: true,
  })
})

describe('useSpeech', () => {
  it('returns a speak function', () => {
    const { result } = renderHook(() => useSpeech('en-US'))
    expect(typeof result.current.speak).toBe('function')
  })

  it('calls speechSynthesis.cancel then speak when invoked', () => {
    const { result } = renderHook(() => useSpeech('pt-BR'))
    act(() => { result.current.speak('Comer') })
    expect(mockCancel).toHaveBeenCalledOnce()
    expect(mockSpeak).toHaveBeenCalledOnce()
  })

  it('creates an utterance with the correct language', () => {
    const { result } = renderHook(() => useSpeech('es-ES'))
    act(() => { result.current.speak('Comer') })
    const utterance = mockSpeak.mock.calls[0][0] as SpeechSynthesisUtterance
    expect(utterance.lang).toBe('es-ES')
  })

  it('creates an utterance with the correct text', () => {
    const { result } = renderHook(() => useSpeech('en-US'))
    act(() => { result.current.speak('Hello') })
    const utterance = mockSpeak.mock.calls[0][0] as SpeechSynthesisUtterance
    expect(utterance.text).toBe('Hello')
  })

  it('does not speak when speechSynthesis is unavailable', () => {
    Object.defineProperty(window, 'speechSynthesis', {
      value: undefined,
      writable: true,
      configurable: true,
    })
    // @ts-expect-error
    delete (window as any).speechSynthesis

    const consoleSpy = vi.spyOn(console, 'warn').mockImplementation(() => {})
    const { result } = renderHook(() => useSpeech('en-US'))
    act(() => { result.current.speak('Hello') })
    expect(mockSpeak).not.toHaveBeenCalled()
    expect(consoleSpy).toHaveBeenCalledWith(expect.stringContaining('not supported'))
    consoleSpy.mockRestore()
  })

  it('picks a matching voice when one is available', () => {
    const matchingVoice = { lang: 'pt-BR', name: 'Portuguese' } as SpeechSynthesisVoice
    mockGetVoices.mockReturnValue([matchingVoice])
    const { result } = renderHook(() => useSpeech('pt-BR'))
    act(() => { result.current.speak('Brincar') })
    const utterance = mockSpeak.mock.calls[0][0] as SpeechSynthesisUtterance
    expect(utterance.voice).toBe(matchingVoice)
  })
})
