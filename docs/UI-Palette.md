# Material Professional Light - UI Palette & Style Guide

## Overview
This document defines the design system for the Train Ticket Management System (QLVeTau) using Material Professional Light theme.

## Design Philosophy
- **Flat Design**: No shadows or 3D effects
- **Clean & Professional**: Minimalist approach with focus on functionality
- **Material Light**: Following Google's Material Design principles with a light color scheme
- **Consistency**: Uniform spacing, colors, and components throughout the application

## Color Palette

### Primary Colors
| Color Name | Hex Code | Usage |
|------------|----------|-------|
| Primary Blue | `#1976D2` | Primary buttons, accents, links, active states |
| Primary Blue Dark | `#0D47A1` | Button hover states, selected items |
| Light Blue | `#2196F3` | Table headers, secondary accents |
| Very Light Blue | `#E3F2FD` | Selection backgrounds, tab backgrounds |

### Neutral Colors
| Color Name | Hex Code | Usage |
|------------|----------|-------|
| Background Gray | `#F5F5F5` | Main background, panel backgrounds |
| White | `#FFFFFF` | Control backgrounds (inputs, tables, cards) |
| Light Gray | `#E0E0E0` | Borders, disabled buttons, dividers |
| Medium Gray | `#BDBDBD` | Border colors, scrollbar thumb |
| Dark Gray | `#9E9E9E` | Disabled text, placeholder text |
| Text Gray | `#757575` | Secondary text, unselected tabs |
| Text Dark | `#212121` | Primary text color |

## Typography

### Font Family
- **Primary**: Roboto (Regular, Medium, Bold)
- **Fallback**: Segoe UI, System Default

### Font Sizes
| Element | Size | Weight |
|---------|------|--------|
| Headings (H1) | 24px | Bold |
| Headings (H2) | 20px | Bold |
| Headings (H3) | 18px | Medium |
| Body Text | 14px | Regular |
| Small Text | 12px | Regular |
| Button Text | 14px | Medium |
| Navigation Text | 14px | Medium |

### Text Color Hierarchy
1. **Primary Text**: `#212121` - Main content
2. **Secondary Text**: `#757575` - Supporting information
3. **Disabled Text**: `#9E9E9E` - Inactive elements
4. **Link/Active Text**: `#1976D2` - Interactive elements

## Component Guidelines

### Buttons
- **Border Radius**: 6px
- **Padding**: 8px 16px
- **Height**: 36px (standard), 40px (large)
- **Background**: `#1976D2`
- **Text**: White, 14px, Medium weight
- **Hover**: `#0D47A1`
- **Disabled**: `#E0E0E0` background, `#9E9E9E` text
- **Border**: None (flat design)

### Text Fields
- **Border Radius**: 6px
- **Height**: 36px
- **Background**: White
- **Border**: 1px solid `#BDBDBD`
- **Focus Border**: 2px solid `#1976D2`
- **Padding**: 8px 12px
- **Placeholder**: `#9E9E9E`

### Navigation Bar
- **Position**: Top (horizontal)
- **Height**: 56px
- **Background**: `#1976D2`
- **Text**: White, 14px, Medium weight
- **Style**: Text-only buttons, no icons
- **Spacing**: 4px between items
- **Padding**: 12px horizontal

### Tables
- **Header Background**: `#2196F3`
- **Header Text**: White, 14px, Medium weight
- **Header Height**: 32px
- **Row Height**: 28px
- **Border**: None between columns, 1px `#E0E0E0` between rows
- **Selection Background**: `#E3F2FD`
- **Selection Text**: `#0D47A1`
- **Alternate Row**: Same as background (no striping)

### Tabs
- **Height**: 40px
- **Padding**: 6px 12px
- **Selected Background**: `#E3F2FD`
- **Selected Text**: `#1976D2`, Medium weight
- **Unselected Text**: `#757575`, Regular weight
- **Underline**: 2px `#1976D2` for selected tab
- **Hover**: `#E0E0E0` background

### Panels & Cards
- **Background**: White
- **Border Radius**: 6px
- **Border**: 1px solid `#E0E0E0` (optional)
- **Padding**: 16px
- **Shadow**: None (flat design)

### Scroll Bars
- **Width**: 12px
- **Border Radius**: 6px
- **Thumb**: `#BDBDBD`
- **Thumb Hover**: `#9E9E9E`
- **Thumb Pressed**: `#757575`
- **Track**: `#F5F5F5`

### Checkboxes & Radio Buttons
- **Border Radius**: 6px (checkbox), 50% (radio)
- **Size**: 18px
- **Border**: 2px solid `#BDBDBD`
- **Checked Border**: 2px solid `#1976D2`
- **Checked Background**: `#1976D2`
- **Checkmark**: White

### Combo Boxes (Dropdowns)
- **Border Radius**: 6px
- **Height**: 36px
- **Background**: White
- **Border**: 1px solid `#BDBDBD`
- **Arrow Color**: `#212121`
- **Item Hover**: `#E3F2FD`

## Spacing System

### Standard Spacing Units
- **XS**: 4px
- **S**: 8px
- **M**: 12px
- **L**: 16px
- **XL**: 20px
- **XXL**: 24px

### Component Spacing
- **Form Fields**: 12px vertical spacing
- **Sections**: 20px vertical spacing
- **Panel Margins**: 16px
- **Button Spacing**: 8px horizontal gap

## Layout Guidelines

### General Principles
1. **Consistency**: Use the same spacing and sizing throughout
2. **Alignment**: Left-align text and form labels
3. **White Space**: Use adequate spacing to avoid crowding
4. **Responsive**: Components should adapt to window size
5. **No Fixed Sizes**: Use layout managers for flexibility

### Navigation Structure
The navigation order must remain unchanged:
1. Trang chủ (Home)
2. Vé (Tickets) - Dropdown
   - Đặt vé (Book Ticket)
   - Hoàn vé (Refund Ticket)
   - Xuất hóa đơn (Invoice)
3. Khách hàng (Customer)
4. Tàu (Train)
5. Chuyến Tàu (Train Schedule)
6. Nhân viên (Employee) - Dropdown (Manager only)
   - Quản lý nhân viên (Manage Employees)
   - Thống kê (Statistics)
   - Dữ liệu đã xóa (Deleted Data)
7. Tài khoản (Account) (Manager only)
8. Đăng xuất (Logout)

## Implementation Notes

### Do's ✅
- Use the defined color palette consistently
- Apply 6px border radius to all components
- Use Roboto font throughout (with fallback)
- Keep flat design - no shadows
- Maintain consistent spacing
- Use Material colors for states (hover, active, disabled)
- Keep navigation order unchanged
- Preserve all existing functionality and event handlers

### Don'ts ❌
- Don't add shadows or 3D effects
- Don't change navigation order
- Don't modify DAO, Service, or Controller logic
- Don't change form layouts or component structure
- Don't add new dependencies beyond FlatLaf
- Don't remove or modify event listeners
- Don't use custom fonts besides Roboto/Segoe UI
- Don't use colors outside the defined palette

## Testing Checklist
- [ ] All buttons have correct styling and hover effects
- [ ] Text fields have proper borders and focus states
- [ ] Tables display correctly with proper headers
- [ ] Navigation bar is positioned at top and works correctly
- [ ] All forms maintain their original functionality
- [ ] Color palette is applied consistently
- [ ] Font is Roboto throughout (or fallback)
- [ ] No functionality is broken
- [ ] All event handlers still work
- [ ] Window resizes properly

## Version
- **Version**: 1.0
- **Last Updated**: December 2024
- **Theme**: Material Professional Light
