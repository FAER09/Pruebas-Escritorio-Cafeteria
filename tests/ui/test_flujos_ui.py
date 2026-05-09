import unittest
from pathlib import Path

from tests.ui.support import (
    CONDIMENTOS_BASE,
    DataFilesBackup,
    EMPLEADO_TITLE,
    LOGIN_TITLE,
    assert_any_dialog_appears_and_close,
    assert_dialog_appears_and_close,
    capture_evidence,
    click_employee_add,
    click_employee_finalize,
    click_first_condiment,
    close_any_java_windows,
    compile_app,
    fill_login,
    launch_app,
    stop_process,
    wait_for_window,
    write_base_data,
    maybe_get_window,
    message_title_candidates,
    PROJECT_ROOT,
    select_first_beverage,
    TICKET_TITLE,
)


class FlujosEmpleadoUiTests(unittest.TestCase):
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

    def iniciar_sesion_como_empleado(self):
        self.process = launch_app()
        login = wait_for_window(LOGIN_TITLE)
        fill_login(login, "empleado1", "emp123")
        return wait_for_window(EMPLEADO_TITLE)

    def finalizar_bebida_agregada(self, empleado, evidencia_ticket: str) -> None:
        click_employee_finalize(empleado)
        assert_dialog_appears_and_close(TICKET_TITLE(), evidencia_ticket)
        self.assertIsNotNone(maybe_get_window(EMPLEADO_TITLE))

    def test_ui_03a_sin_condimento_marcado(self) -> None:
        empleado = self.iniciar_sesion_como_empleado()
        select_first_beverage(empleado)
        click_employee_add(empleado)
        self.finalizar_bebida_agregada(empleado, "UI-03a-ticket")
        compras = Path(PROJECT_ROOT / "compras.txt").read_text(encoding="utf-8")

        self.assertIn("Espresso", compras)
        self.assertIn("$25.00", compras)
        capture_evidence("UI-03a")


    def test_ui_03b_un_condimento_marcado(self) -> None:
        empleado = self.iniciar_sesion_como_empleado()
        select_first_beverage(empleado)
        click_first_condiment(empleado)
        click_employee_add(empleado)
        self.finalizar_bebida_agregada(empleado, "UI-03b-ticket")
        compras = Path(PROJECT_ROOT / "compras.txt").read_text(encoding="utf-8")

        self.assertIn("Espresso + Leche", compras)
        self.assertIn("$30.00", compras)
        capture_evidence("UI-03b")

    def test_ui_04a_sin_pedidos_agregados(self) -> None:
        contenido_inicial = (PROJECT_ROOT / "compras.txt").read_text(encoding="utf-8")
        empleado = self.iniciar_sesion_como_empleado()
        click_employee_finalize(empleado)
        assert_any_dialog_appears_and_close(message_title_candidates(), "UI-04a")
        contenido_final = (PROJECT_ROOT / "compras.txt").read_text(encoding="utf-8")

        self.assertEqual(contenido_inicial, contenido_final)
        self.assertIsNotNone(maybe_get_window(EMPLEADO_TITLE))


    def test_ui_04b_con_pedidos_todo_correcto(self) -> None:
        empleado = self.iniciar_sesion_como_empleado()
        select_first_beverage(empleado)
        click_employee_add(empleado)
        self.finalizar_bebida_agregada(empleado, "UI-04b")
        texto = Path(PROJECT_ROOT / "compras.txt").read_text(encoding="utf-8")
        self.assertIn("Empleado: empleado1", texto)
        self.assertIn("Espresso", texto)
        self.assertIn("$25.00", texto)


if __name__ == "__main__":
    unittest.main(verbosity=2)
