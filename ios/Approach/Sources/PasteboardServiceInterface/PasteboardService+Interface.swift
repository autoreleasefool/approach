import Dependencies

public struct PasteboardService: Sendable {
	public var copyToClipboard: @Sendable (String) -> Void

	public init(
		copyToClipboard: @escaping @Sendable (String) -> Void
	) {
		self.copyToClipboard = copyToClipboard
	}
}

extension PasteboardService: TestDependencyKey {
	public static var testValue = Self(
		copyToClipboard: { _ in unimplemented("\(Self.self).copyToClipboard") }
	)
}

extension DependencyValues {
	public var pasteboard: PasteboardService {
		get { self[PasteboardService.self] }
		set { self[PasteboardService.self] = newValue }
	}
}
