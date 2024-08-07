-- 컬럼 타입을 LineStringZ로 변경
ALTER TABLE running_record
    ALTER COLUMN route TYPE GEOMETRY(LineStringZ, 4326) USING ST_Force3D(route);
