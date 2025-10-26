# Quick Start Guide - My Vet App

## 🚀 What's New

### New Features
- ⭐ **Feedback Screen**: Rate your experience with 1-5 stars and leave suggestions
- 🔐 **Registration Flow**: Fixed and properly integrated
- 💾 **Backend Integration**: All data now saved to MongoDB
- 🔄 **CRUD Operations**: Full Create, Read, Update, Delete for mascotas and citas

### Bug Fixes
- ✅ Registration navigation working correctly
- ✅ No more app crashes on save
- ✅ User stays in app after operations
- ✅ Session expiry handled gracefully
- ✅ All API calls properly authenticated with JWT

## 🎯 Quick Testing Checklist

### Basic Flow Test (5 minutes)
```
1. ✅ Open app → Should show login screen
2. ✅ Tap "Crear cuenta" → Register new user
3. ✅ Auto-login → Navigate to home screen
4. ✅ Register a mascota → Tap "Registrar Mascota" in Perfil
5. ✅ Create a cita → Go to Citas tab, create appointment
6. ✅ Submit feedback → Go to Feedback tab, rate 5 stars
7. ✅ Logout → Tap "Cerrar sesión" in Perfil tab
8. ✅ Login again → Verify data persists
```

### Advanced Flow Test (10 minutes)
```
1. ✅ Prediagnóstico → AI diagnosis
   - Enter symptoms
   - Navigate to Citas with pre-filled motivo
   
2. ✅ Mascota Management
   - List mascotas (in Perfil or Gestión)
   - Edit a mascota
   - Verify changes persist
   
3. ✅ Profile Screen
   - View mascotas count
   - View citas count
   - Edit profile information
   
4. ✅ Error Handling
   - Try operation without internet (should show error)
   - Session expiry (should prompt to login)
```

## 📱 Navigation Map

```
Login Screen (auth_screen)
    ↓ (after login)
    ├─ Dueño Role → Main Screen (5 tabs + 3 sub-screens)
    │   ├─ [Home] - Welcome message
    │   ├─ [Citas] - Book appointments
    │   ├─ [Pre-diagnóstico] - AI symptoms checker
    │   ├─ [Historial] - View appointment history
    │   ├─ [Feedback] ⭐ NEW - Rate experience
    │   ├─ [Perfil] - View/manage profile
    │   │   ├─ Editar Perfil
    │   │   ├─ Registrar Mascota
    │   │   └─ Gestionar Mascotas
    │   
    └─ Veterinario Role → Veterinario Screen (3 tabs)
        ├─ Personas - View all owners
        ├─ Citas - View all appointments
        └─ Mascotas - View all pets
```

## 🔑 Key Files Changed

### New Files
- `network/FeedbackApi.kt` - Feedback API endpoints
- `feedback/FeedbackScreen.kt` - Feedback UI
- `STABILIZATION_SUMMARY.md` - Complete documentation

### Modified Files
- `MainActivity.kt` - Added register_screen route
- `MainScreen.kt` - Added Feedback tab
- `OwnerRepository.kt` - Added CRUD operations
- `GestionMascotasScreen.kt` - Uses backend API
- `PerfilScreen.kt` - Loads from backend API

## 🔧 Backend Configuration

**Base URL**: `http://10.0.2.2:4000/`  
(This is the Android emulator's localhost)

**Authentication**: JWT Bearer token  
**Storage**: SharedPreferences (`auth_prefs`)

### API Endpoints Used
```
Auth:
  POST /api/auth/register
  POST /api/auth/login

Owners:
  GET/POST/PUT/DELETE /api/owners/me/mascotas
  GET/POST/PUT/DELETE /api/owners/me/citas
  POST /api/owners/me/profile

AI:
  POST /api/ai/prediagnostico

Feedback:
  POST /api/feedback
  GET /api/feedback/mine
```

## 🐛 Troubleshooting

### "Sesión expirada" Error
- **Cause**: JWT token expired
- **Solution**: Logout and login again

### "Error al cargar mascotas"
- **Cause**: Backend not running or network error
- **Solution**: 
  1. Check backend is running on port 4000
  2. Check emulator network connectivity
  3. Verify `http://10.0.2.2:4000/` is accessible

### "No se pudieron cargar mascotas"
- **Cause**: No mascotas registered yet
- **Solution**: Register at least one mascota first

### App crashes on save
- **Fixed**: All save operations now use proper error handling
- **Fallback**: Shows error toast, stays in app

## 📊 Data Flow

```
Screen Action
    ↓
ViewModel (optional)
    ↓
Repository
    ↓
RetrofitClient.authed(context)
    ↓
TokenInterceptor (adds JWT)
    ↓
Backend API
    ↓
MongoDB
```

## 🎨 UX Improvements

1. **Loading States**: Shows "Cargando..." during API calls
2. **Success Feedback**: Toast messages on successful operations
3. **Error Handling**: User-friendly error messages
4. **No Crashes**: All network errors caught and handled
5. **Stay in App**: No unexpected app closes
6. **Visual Feedback**: Star rating with color changes

## 📝 Notes for Developers

- All authenticated requests use `RetrofitClient.authed(context)`
- TokenInterceptor automatically adds JWT header
- 401 errors should trigger logout flow
- Use `withContext(Dispatchers.IO)` for network calls
- Use `Toast.makeText()` for user feedback
- Loading states prevent duplicate submissions

## ✅ Verification Checklist

Before submitting to production:
- [ ] All API endpoints return expected data
- [ ] JWT tokens refresh correctly
- [ ] Offline mode handled gracefully
- [ ] All CRUD operations persist to database
- [ ] Session expiry redirects to login
- [ ] No memory leaks in list screens
- [ ] Images load correctly (if implemented)
- [ ] Notifications work (if enabled)

## 🔗 Related Documentation

- `STABILIZATION_SUMMARY.md` - Complete technical details
- Backend API docs - Check backend repository
- Compose guidelines - https://developer.android.com/jetpack/compose

---

**Last Updated**: December 2024  
**Version**: 1.0  
**Status**: ✅ Ready for Testing
