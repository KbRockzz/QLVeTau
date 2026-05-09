-- Allow storing full Vietnamese ticket type names such as "Người khuyết tật".
ALTER TABLE Ve MODIFY COLUMN loaiVe VARCHAR(100);
