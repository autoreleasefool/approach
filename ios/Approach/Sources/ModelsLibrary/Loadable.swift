public enum Loadable<Value, Failure> {
	case notLoaded
	case loading(Value?)
	case loaded(Value)
	case failed(Value?, Failure?)

	public var value: Value? {
		switch self {
		case .notLoaded:
			nil
		case let .loading(t), let .failed(t, _):
			t
		case .loaded(let t):
			t
		}
	}

	public mutating func startLoading() {
		self = .loading(self.value)
	}
}

extension Loadable: Equatable where Value: Equatable, Failure: Equatable {}
extension Loadable: Sendable where Value: Sendable, Failure: Sendable {}
