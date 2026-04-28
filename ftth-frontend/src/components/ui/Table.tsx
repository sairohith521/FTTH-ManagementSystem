import type { ReactNode } from "react";

interface Column<T> {
  key: string;
  header: string;
  render?: (row: T) => ReactNode;
}

interface Props<T> {
  columns: Column<T>[];
  data: T[];
  keyField: keyof T;
}

export default function Table<T>({ columns, data, keyField }: Props<T>) {
  return (
    <div className="overflow-x-auto">
      <table>
        <thead>
          <tr className="border-b border-border">
            {columns.map((col) => (
              <th key={col.key} className="py-2 px-3">{col.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="py-4 text-center text-textSecondary">
                No data found.
              </td>
            </tr>
          ) : (
            data.map((row) => (
              <tr key={String(row[keyField])} className="border-b border-border hover:bg-background">
                {columns.map((col) => (
                  <td key={col.key} className="py-2 px-3">
                    {col.render ? col.render(row) : String((row as Record<string, unknown>)[col.key] ?? "")}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
