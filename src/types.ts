export type Language = 'en-US' | 'es-ES' | 'pt-BR'

export interface CardData {
  id: string
  emoji: string
  labels: Record<Language, string>
  color: string
}
