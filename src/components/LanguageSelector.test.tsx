import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { LanguageSelector } from './LanguageSelector'

describe('LanguageSelector', () => {
  it('renders three language buttons', () => {
    render(<LanguageSelector language="en-US" onChange={vi.fn()} />)
    expect(screen.getByRole('button', { name: 'English' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Español' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Português' })).toBeInTheDocument()
  })

  it('marks the active language as pressed', () => {
    render(<LanguageSelector language="pt-BR" onChange={vi.fn()} />)
    expect(screen.getByRole('button', { name: 'Português' })).toHaveAttribute('aria-pressed', 'true')
    expect(screen.getByRole('button', { name: 'English' })).toHaveAttribute('aria-pressed', 'false')
    expect(screen.getByRole('button', { name: 'Español' })).toHaveAttribute('aria-pressed', 'false')
  })

  it('calls onChange with the correct language code when a button is clicked', async () => {
    const onChange = vi.fn()
    render(<LanguageSelector language="en-US" onChange={onChange} />)
    await userEvent.click(screen.getByRole('button', { name: 'Español' }))
    expect(onChange).toHaveBeenCalledOnce()
    expect(onChange).toHaveBeenCalledWith('es-ES')
  })

  it('has an accessible group label', () => {
    render(<LanguageSelector language="en-US" onChange={vi.fn()} />)
    expect(screen.getByRole('group', { name: 'Select language' })).toBeInTheDocument()
  })
})
