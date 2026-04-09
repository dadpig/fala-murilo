import type { CardData } from '../types'

export const CARDS: CardData[] = [
  {
    id: 'eat',
    emoji: '🍽️',
    labels: { 'en-US': 'Eat', 'es-ES': 'Comer', 'pt-BR': 'Comer' },
    color: '#FF6B6B',
  },
  {
    id: 'drink',
    emoji: '🥤',
    labels: { 'en-US': 'Drink', 'es-ES': 'Beber', 'pt-BR': 'Beber' },
    color: '#4ECDC4',
  },
  {
    id: 'play',
    emoji: '🎮',
    labels: { 'en-US': 'Play', 'es-ES': 'Jugar', 'pt-BR': 'Brincar' },
    color: '#45B7D1',
  },
  {
    id: 'sleep',
    emoji: '😴',
    labels: { 'en-US': 'Sleep', 'es-ES': 'Dormir', 'pt-BR': 'Dormir' },
    color: '#96CEB4',
  },
  {
    id: 'dad',
    emoji: '👨',
    labels: { 'en-US': 'Dad', 'es-ES': 'Papá', 'pt-BR': 'Papai' },
    color: '#FFEAA7',
  },
  {
    id: 'mom',
    emoji: '👩',
    labels: { 'en-US': 'Mom', 'es-ES': 'Mamá', 'pt-BR': 'Mamãe' },
    color: '#DDA0DD',
  },
  {
    id: 'yes',
    emoji: '✅',
    labels: { 'en-US': 'Yes', 'es-ES': 'Sí', 'pt-BR': 'Sim' },
    color: '#98D8C8',
  },
  {
    id: 'no',
    emoji: '❌',
    labels: { 'en-US': 'No', 'es-ES': 'No', 'pt-BR': 'Não' },
    color: '#F7A8A8',
  },
  {
    id: 'help',
    emoji: '🙋',
    labels: { 'en-US': 'Help', 'es-ES': 'Ayuda', 'pt-BR': 'Ajuda' },
    color: '#FFD700',
  },
]

export const PHOTO_STORAGE_PREFIX = 'fala-murilo:photo:'
