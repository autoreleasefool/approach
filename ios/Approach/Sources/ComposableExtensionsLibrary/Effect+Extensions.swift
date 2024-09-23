import ComposableArchitecture

extension Effect {
	public static func cancelling<ID: Hashable & Sendable>(id: ID) -> Self {
		.run { _ in
			do {
				try await Task.never()
			} catch is CancellationError {
				Task.cancel(id: id)
			}
		}
	}
}
