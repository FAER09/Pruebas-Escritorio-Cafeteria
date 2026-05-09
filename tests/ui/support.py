from __future__ import annotations

import ctypes
import os
import subprocess
import time
import tkinter as tk
from pathlib import Path

import pyautogui
import pygetwindow as gw

PROJECT_ROOT = Path(__file__).resolve().parents[2]
EVIDENCE_DIR = PROJECT_ROOT / "tests" / "ui" / "evidencias"
LOGIN_TITLE = "Login - Cafeter"
GERENTE_TITLE = "Gerente - Cafeter"
EMPLEADO_TITLE = "Empleado - Cafeter"
KNOWN_DIALOG_TITLES = ("Error", "Message", "Mensaje", "Ticket")

USUARIOS_BASE = "admin|admin123|GERENTE\nempleado1|emp123|EMPLEADO\n"
BEBIDAS_BASE = (
    "Espresso|25.00|true\n"
    "Americano|30.00|true\n"
    "Cappuccino|40.00|true\n"
    "Latte|45.00|true\n"
)
CONDIMENTOS_BASE = (
    "Leche|5.00|true\n"
    "Leche de Almendra|10.00|true\n"
    "Azucar|2.00|true\n"
    "Canela|3.00|true\n"
    "Vainilla|5.00|true\n"
)

pyautogui.FAILSAFE = True
pyautogui.PAUSE = 0.2
EVIDENCE_DIR.mkdir(parents=True, exist_ok=True)
USER32 = ctypes.windll.user32
SW_RESTORE = 9


class DataFilesBackup:
    def __init__(self, *relative_paths: str) -> None:
        self.paths = [PROJECT_ROOT / path for path in relative_paths]
        self._backup: dict[Path, bytes] = {}
        self._existed: dict[Path, bool] = {}

    def __enter__(self) -> "DataFilesBackup":
        for path in self.paths:
            ensure_writable(path)
            self._existed[path] = path.exists()
            self._backup[path] = path.read_bytes() if path.exists() else b""
        return self

    def write(self, relative_path: str, content: str) -> None:
        path = PROJECT_ROOT / relative_path
        ensure_writable(path)
        path.write_text(content, encoding="utf-8")

    def make_read_only(self, relative_path: str) -> None:
        os.chmod(PROJECT_ROOT / relative_path, 0o444)

    def close(self) -> None:
        for path in self.paths:
            if self._existed[path]:
                try:
                    os.chmod(path, 0o666)
                except OSError:
                    pass
                path.write_bytes(self._backup[path])
            elif path.exists():
                path.unlink()

    def __exit__(self, exc_type, exc, tb) -> None:
        self.close()


def compile_app() -> None:
    command = (
        "if (-not (Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }\n"
        "javac -encoding UTF-8 -d out -sourcepath src "
        "src\\Main.java src\\controlador\\*.java src\\modelo\\*.java "
        "src\\persistencia\\*.java src\\util\\*.java src\\vista\\*.java"
    )
    subprocess.run(
        ["powershell", "-NoProfile", "-Command", command],
        cwd=PROJECT_ROOT,
        check=True,
        capture_output=True,
        text=True,
    )


def ensure_writable(path: Path) -> None:
    if path.exists():
        try:
            os.chmod(path, 0o666)
        except OSError:
            pass


def write_base_data(condimentos: str | None = None, compras: str = "") -> None:
    usuarios = PROJECT_ROOT / "usuarios.txt"
    bebidas = PROJECT_ROOT / "bebidas.txt"
    condimentos_path = PROJECT_ROOT / "condimentos.txt"
    compras_path = PROJECT_ROOT / "compras.txt"

    for path in (usuarios, bebidas, condimentos_path, compras_path):
        ensure_writable(path)

    usuarios.write_text(USUARIOS_BASE, encoding="utf-8")
    bebidas.write_text(BEBIDAS_BASE, encoding="utf-8")
    condimentos_path.write_text(condimentos or CONDIMENTOS_BASE, encoding="utf-8")
    compras_path.write_text(compras, encoding="utf-8")


def launch_app() -> subprocess.Popen[str]:
    return subprocess.Popen(
        ["java", "-cp", "out", "Main"],
        cwd=PROJECT_ROOT,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
        text=True,
    )


def wait_for_window(title_fragment: str, timeout: float = 15.0):
    deadline = time.time() + timeout
    while time.time() < deadline:
        for window in gw.getAllWindows():
            if title_fragment in window.title:
                return window
        time.sleep(0.2)
    raise AssertionError(f"No se encontro ventana con titulo que contenga: {title_fragment}")


def maybe_get_window(title_fragment: str):
    for window in gw.getAllWindows():
        if title_fragment in window.title:
            return window
    return None


def wait_until_window_closes(title_fragment: str, timeout: float = 5.0) -> None:
    deadline = time.time() + timeout
    while time.time() < deadline:
        if maybe_get_window(title_fragment) is None:
            return
        time.sleep(0.2)
    raise AssertionError(f"La ventana '{title_fragment}' no se cerro dentro del tiempo esperado.")


def activate_window(window) -> None:
    if window.isMinimized:
        window.restore()
    try:
        window.activate()
    except Exception:
        try:
            hwnd = int(window._hWnd)
            USER32.ShowWindow(hwnd, SW_RESTORE)
            USER32.BringWindowToTop(hwnd)
            USER32.SetForegroundWindow(hwnd)
        except Exception:
            pass
    x = int(window.left + max(window.width // 2, 20))
    y = int(window.top + min(max(window.height // 10, 10), max(window.height - 10, 10)))
    pyautogui.click(x, y)
    time.sleep(0.5)


def click_relative(window, x_ratio: float, y_ratio: float) -> None:
    x = int(window.left + window.width * x_ratio)
    y = int(window.top + window.height * y_ratio)
    pyautogui.click(x, y)


def fill_login(window, user: str, password: str) -> None:
    activate_window(window)
    click_relative(window, 0.68, 0.39)
    pyautogui.hotkey("ctrl", "a")
    pyautogui.press("backspace")
    pyautogui.write(user, interval=0.03)
    click_relative(window, 0.68, 0.58)
    pyautogui.hotkey("ctrl", "a")
    pyautogui.press("backspace")
    pyautogui.write(password, interval=0.03)
    click_relative(window, 0.68, 0.80)


def read_clipboard() -> str:
    root = tk.Tk()
    root.withdraw()
    try:
        root.update()
        return root.clipboard_get()
    except tk.TclError:
        return ""
    finally:
        root.destroy()


def capture_evidence(name: str) -> Path:
    path = EVIDENCE_DIR / f"{name}.png"
    pyautogui.screenshot(str(path))
    return path


def close_dialog_with_enter(title_fragment: str, timeout: float = 5.0) -> None:
    dialog = wait_for_window(title_fragment, timeout=timeout)
    activate_window(dialog)
    pyautogui.press("enter")
    wait_until_window_closes(title_fragment, timeout=timeout)


def assert_dialog_appears_and_close(title_fragment: str, evidence_name: str | None = None):
    dialog = wait_for_window(title_fragment, timeout=5.0)
    if evidence_name is not None:
        capture_evidence(evidence_name)
    activate_window(dialog)
    pyautogui.press("enter")
    wait_until_window_closes(title_fragment, timeout=5.0)
    return dialog


def close_any_java_windows() -> None:
    for fragment in (TICKET_TITLE(), GERENTE_TITLE, EMPLEADO_TITLE, LOGIN_TITLE, "Error", "Message", "Mensaje"):
        while True:
            window = maybe_get_window(fragment)
            if window is None:
                break
            try:
                activate_window(window)
                pyautogui.hotkey("alt", "f4")
                time.sleep(0.5)
            except Exception:
                break


def stop_process(process: subprocess.Popen[str] | None) -> None:
    if process is None:
        return
    if process.poll() is None:
        process.terminate()
        try:
            process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            process.kill()


def ticket_title_candidates() -> list[str]:
    return ["Ticket", "Mensaje", "Message"]


def TICKET_TITLE() -> str:
    return "Ticket"


def message_title_candidates() -> list[str]:
    return ["Message", "Mensaje"]


def wait_for_any_window(title_fragments: list[str], timeout: float = 5.0):
    deadline = time.time() + timeout
    while time.time() < deadline:
        for fragment in title_fragments:
            window = maybe_get_window(fragment)
            if window is not None:
                return window
        time.sleep(0.2)
    raise AssertionError(f"No se encontro ninguna ventana esperada: {title_fragments}")


def assert_any_dialog_appears_and_close(title_fragments: list[str], evidence_name: str | None = None):
    dialog = wait_for_any_window(title_fragments, timeout=5.0)
    if evidence_name is not None:
        capture_evidence(evidence_name)
    dialog_title = dialog.title
    activate_window(dialog)
    pyautogui.press("enter")
    wait_until_window_closes(dialog_title, timeout=5.0)
    return dialog


def copy_text_area(window, x_ratio: float = 0.84, y_ratio: float = 0.38) -> str:
    activate_window(window)
    click_relative(window, x_ratio, y_ratio)
    pyautogui.hotkey("ctrl", "a")
    pyautogui.hotkey("ctrl", "c")
    time.sleep(0.3)
    return read_clipboard()


def select_first_beverage(window) -> None:
    activate_window(window)
    click_relative(window, 0.30, 0.18)
    time.sleep(0.2)
    pyautogui.press("home")
    pyautogui.press("enter")
    time.sleep(0.3)


def click_employee_add(window) -> None:
    activate_window(window)
    click_relative(window, 0.40, 0.92)


def click_employee_finalize(window) -> None:
    activate_window(window)
    click_relative(window, 0.50, 0.92)


def click_first_condiment(window) -> None:
    activate_window(window)
    click_relative(window, 0.27, 0.29)


def login_as(user: str, password: str):
    process = launch_app()
    login_window = wait_for_window(LOGIN_TITLE)
    fill_login(login_window, user, password)
    return process
