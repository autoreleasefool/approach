import Dependencies

public struct EmailService: Sendable {
	public var canSendEmail: @Sendable () async -> Bool

	public init(
		canSendEmail: @escaping @Sendable () async -> Bool
	) {
		self.canSendEmail = canSendEmail
	}
}

extension EmailService: TestDependencyKey {
	public static var testValue = Self(
		canSendEmail: { unimplemented("\(Self.self).canSendEmail") }
	)
}
