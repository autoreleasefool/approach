public struct AlwaysEqual<V>: Equatable {
	public let wrapped: V
	public init(_ wrapped: V) {
		self.wrapped = wrapped
	}

	public static func == (lhs: Self, rhs: Self) -> Bool {
		true
	}
}
