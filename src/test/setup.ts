import '@testing-library/jest-dom'

const localStorageStore: Record<string, string> = {}
const localStorageMock: Storage = {
  getItem: (key) => localStorageStore[key] ?? null,
  setItem: (key, value) => { localStorageStore[key] = value },
  removeItem: (key) => { delete localStorageStore[key] },
  clear: () => { Object.keys(localStorageStore).forEach((k) => delete localStorageStore[k]) },
  get length() { return Object.keys(localStorageStore).length },
  key: (index) => Object.keys(localStorageStore)[index] ?? null,
}
Object.defineProperty(window, 'localStorage', { value: localStorageMock, writable: false })

class SpeechSynthesisUtteranceMock {
  text: string
  lang = ''
  rate = 1
  pitch = 1
  volume = 1
  voice: SpeechSynthesisVoice | null = null
  constructor(text: string) { this.text = text }
}
// @ts-expect-error
window.SpeechSynthesisUtterance = SpeechSynthesisUtteranceMock
