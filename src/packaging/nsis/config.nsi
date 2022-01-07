;NSIS Modern User Interface
;Welcome/Finish Page Example Script
;Written by Joost Verburg

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ; Expecting the following symbols from caller:
  ;   PROJECT_NAME
  ;   PROJECT_VERSION
  ;   MAIN_JAR_FILE

  ; MUI Settings / Icons
  !define MUI_ICON "logo.ico"
  ;!define MUI_UNICON ;Uninstall icon

  ; MUI Settings / Header
  ; !define MUI_HEADERIMAGE
  ; !define MUI_HEADERIMAGE_RIGHT
  ; !define MUI_HEADERIMAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Header\orange-r-nsis.bmp"
  ; !define MUI_HEADERIMAGE_UNBITMAP "${NSISDIR}\Contrib\Graphics\Header\orange-uninstall-r-nsis.bmp"

  ; MUI Settings / Wizard
  !define MUI_WELCOMEFINISHPAGE_BITMAP "left_side.bmp"
  !define MUI_UNWELCOMEFINISHPAGE_BITMAP "left_side.bmp"

  ;Name and file
  Name "${PROJECT_NAME}"
  OutFile "${OUTPUT_DIR}/${PROJECT_NAME}-${PROJECT_VERSION}-installer.exe"
  Unicode True

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${PROJECT_NAME}"

  ;Get installation folder from registry if available
  InstallDirRegKey HKLM "Software\${PROJECT_NAME}" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel admin

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "LICENSE.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !define MUI_FINISHPAGE_RUN
  !define MUI_FINISHPAGE_RUN_TEXT "Start ${PROJECT_NAME}"
  !define MUI_FINISHPAGE_RUN_FUNCTION "LaunchLink"
  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_COMPONENTS
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Languages

  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Install ${PROJECT_NAME}" SEC0000

  SectionIn RO ;Make it read-only
  SetOutPath "$INSTDIR"
  SetOverwrite on

  ;Files
  File "${MAIN_JAR_FILE}"
  File logo.ico
  File /r lib

  ;Store installation folder
  WriteRegStr HKLM "SOFTWARE\${PROJECT_NAME}" "Install_Dir" "$INSTDIR"

  ;Create uninstaller

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "DisplayName" "${PROJECT_NAME} (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

Section "Create Desktop Shortcut" SEC0001
  SetOutPath "$APPDATA\${PROJECT_NAME}"
  CreateShortCut "$DESKTOP\${PROJECT_NAME}.lnk" "$INSTDIR\${MAIN_JAR_FILE}" "" "$INSTDIR\logo.ico"
SectionEnd

Section "Start Menu Shortcuts" SEC0002

  CreateDirectory "$SMPROGRAMS\${PROJECT_NAME}"
  CreateShortcut "$SMPROGRAMS\${PROJECT_NAME}\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  CreateShortcut "$SMPROGRAMS\${PROJECT_NAME}\${PROJECT_NAME}.lnk" "$INSTDIR\${MAIN_JAR_FILE}" "" "$INSTDIR\logo.ico"

SectionEnd

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

  Delete $INSTDIR\Uninstall.exe
  Delete "$INSTDIR\${MAIN_JAR_FILE}"
  Delete $INSTDIR\lib\*.*
  Delete $INSTDIR\logo.ico

  RMDir $INSTDIR\lib

  Delete $DESKTOP\${PROJECT_NAME}.lnk

  Delete $SMPROGRAMS\${PROJECT_NAME}\Uninstall.lnk
  Delete $SMPROGRAMS\${PROJECT_NAME}\${PROJECT_NAME}.lnk

  RMDir $INSTDIR

  RMDir /r $SMPROGRAMS\${PROJECT_NAME}

  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}"
  DeleteRegKey HKLM "Software\${PROJECT_NAME}"

SectionEnd

Section "un.Remove user data"

  RMDir /r "$APPDATA\${PROJECT_NAME}"

SectionEnd

;--------------------------------
;Functions

Function LaunchLink
  SetOutPath "$APPDATA\${PROJECT_NAME}"
  ExecShell "" "$INSTDIR\${MAIN_JAR_FILE}"
FunctionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "Install ${PROJECT_NAME}."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC0000} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
