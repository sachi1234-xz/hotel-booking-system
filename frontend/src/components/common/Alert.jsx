export function Alert({ type = 'error', message, onClose }) {
  const colors = {
    error: 'bg-red-50 text-red-700 border-red-200',
    success: 'bg-green-50 text-green-700 border-green-200',
    info: 'bg-blue-50 text-blue-700 border-blue-200',
  };
  return (
    <div className={`flex items-center justify-between rounded-lg border p-4 ${colors[type]}`}>
      <span>{message}</span>
      {onClose && (
        <button onClick={onClose} className="ml-4 text-lg font-bold hover:opacity-70">&times;</button>
      )}
    </div>
  );
}
