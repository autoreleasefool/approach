public struct AlwaysEqual<V>: Equatable {
	public let wrapped: V
	public init(wrapped: V) {
		self.wrapped = wrapped
	}

	public static func == (lhs: Self, rhs: Self) -> Bool {
		true
	}
}

public struct NeverEqual<V>: Equatable {
	public let wrapped: V
	public init(wrapped: V) {
		self.wrapped = wrapped
	}

	public static func == (lhs: Self, rhs: Self) -> Bool {
		false
	}
}
