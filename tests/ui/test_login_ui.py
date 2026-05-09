import unittest

from tests.ui.support import (
    DataFilesBackup,
    GERENTE_TITLE,
    LOGIN_TITLE,
    KNOWN_DIALOG_TITLES,
    assert_dialog_appears_and_close,
    compile_app,
    fill_login,
    maybe_get_window,
    stop_process,
    wait_for_window,
    write_base_data,
    launch_app,
    capture_evidence,
    close_any_java_windows,
    EMPLEADO_TITLE,
)


class LoginUiTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        compile_app()

    def setUp(self) -> None:
        self.process = None
        self.backup = DataFilesBackup("usuarios.txt", "bebidas.txt", "condimentos.txt", "compras.txt")
        self.backup.__enter__()
        write_base_data()

    def tearDown(self) -> None:
        close_any_java_windows()
        stop_process(self.process)
        self.backup.close()

    def test_ui_01a_credenciales_incorrectas(self) -> None:
        self.process = launch_app()
        login = wait_for_window(LOGIN_TITLE)
        fill_login(login, "admin", "malpass")
        assert_dialog_appears_and_close("Error", "UI-01a")

        self.assertIsNotNone(maybe_get_window(LOGIN_TITLE))
        self.assertIsNone(maybe_get_window(GERENTE_TITLE))
        for title in KNOWN_DIALOG_TITLES:
            self.assertIsNone(maybe_get_window(title))

    def test_ui_01b_usuario_con_rol_empleado(self) -> None:
        self.process = launch_app()
        login = wait_for_window(LOGIN_TITLE)
        fill_login(login, "empleado1", "emp123")

        self.assertIsNotNone(wait_for_window(EMPLEADO_TITLE))
        self.assertIsNone(maybe_get_window(LOGIN_TITLE))
        self.assertIsNone(maybe_get_window(GERENTE_TITLE))
        capture_evidence("UI-01b")

    def test_ui_01c_credenciales_correctas_gerente(self) -> None:
        self.process = launch_app()
        login = wait_for_window(LOGIN_TITLE)
        fill_login(login, "admin", "admin123")

        self.assertIsNotNone(wait_for_window(GERENTE_TITLE))
        self.assertIsNone(maybe_get_window(LOGIN_TITLE))
        capture_evidence("UI-01c")

    def test_ui_02a_credenciales_totalmente_incorrectas(self) -> None:
        self.process = launch_app()
        login = wait_for_window(LOGIN_TITLE)
        fill_login(login, "fantasma", "malpass")
        assert_dialog_appears_and_close("Error", "UI-02a")

        self.assertIsNotNone(maybe_get_window(LOGIN_TITLE))
        for title in KNOWN_DIALOG_TITLES:
            self.assertIsNone(maybe_get_window(title))

    def test_ui_02b_usuario_correcto_contrasena_incorrecta(self) -> None:
        self.process = launch_app()
        login = wait_for_window(LOGIN_TITLE)
        fill_login(login, "admin", "malpass")
        assert_dialog_appears_and_close("Error", "UI-02b")

        self.assertIsNotNone(maybe_get_window(LOGIN_TITLE))
        for title in KNOWN_DIALOG_TITLES:
            self.assertIsNone(maybe_get_window(title))

    def test_ui_02c_credenciales_correctas(self) -> None:
        self.process = launch_app()
        login = wait_for_window(LOGIN_TITLE)
        fill_login(login, "admin", "admin123")

        self.assertIsNotNone(wait_for_window(GERENTE_TITLE))
        self.assertIsNone(maybe_get_window(LOGIN_TITLE))
        capture_evidence("UI-02c")


if __name__ == "__main__":
    unittest.main(verbosity=2)
