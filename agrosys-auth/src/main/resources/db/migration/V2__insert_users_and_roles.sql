INSERT IGNORE INTO ROL (name, description) VALUES ('SUPERADMIN',	'Tiene acceso a todo el sistema');

INSERT IGNORE INTO MODULE (moduleId, name) VALUES
('1',	'TRABAJADORES'),
('2',	'FINANZAS'),
('3',	'TAREAS'),
('4',	'PARCELAS'),
('5',	'CONFIG'),
('6',	'DASHBOARD');

INSERT IGNORE INTO ROL_MODULE (moduleId, rolId) VALUES
('1',	'1'),
('2',	'1'),
('3',	'1'),
('4',	'1'),
('5',	'1'),
('6',	'1');

INSERT IGNORE INTO USER (userName, password, rolId, firstName, lastName) VALUES 
('admin', '$2a$10$AegXnQoeuiNFnJY0QDQOyeur214BdMHHxEkVIjcy2RyeUKZBsUeva', 1, 'Karla Judith', 'Santos Rivera'),
('villada.edwin@agrosys.com', '$2a$10$t3PI3fVuM6BwkanNu7BSp.cBHcxoz5p1jTHXOyw62IEA0xM15/QeC', 1, 'Edwin', 'Villada Dominguez'),
('yescandon@agrosys.com', '$2a$10$aE3rkDnPQcP/H0EZEg7oPOvFFpsvymyVcpwtmYaOjgVehcJyDW/5C', 1, 'Yatziry', 'Escandon'),
('josue.espinoza@agrosys.com', '$2a$10$JJru5v15QiW5Qhk.V8UlPeLGEHYTCzD7uL1ybwOfJuZ49P/ZxTHx2', 1, 'Josue Said', 'Espinoza Benítez');