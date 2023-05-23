import Dependencies
import Foundation
import ModelsLibrary
import SwiftUI

public struct AvatarService {
	public var render: @Sendable (Avatar.Summary) async -> UIImage?
	public var preRender: @Sendable (Avatar.Summary) async -> Void

	public init(
		render: @escaping @Sendable (Avatar.Summary) async -> UIImage?,
		preRender: @escaping @Sendable (Avatar.Summary) async -> Void
	) {
		self.render = render
		self.preRender = preRender
	}
}

extension AvatarService: TestDependencyKey {
	public static var testValue = Self(
		render: { _ in unimplemented("\(Self.self).render") },
		preRender: { _ in unimplemented("\(Self.self).preRender")}
	)
}

extension DependencyValues {
	public var avatars: AvatarService {
		get { self[AvatarService.self] }
		set { self[AvatarService.self] = newValue }
	}
}
