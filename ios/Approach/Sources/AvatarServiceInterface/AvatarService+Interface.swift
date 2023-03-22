import Dependencies
import Foundation
import SharedModelsLibrary
import SwiftUI

public struct AvatarService {
	public var render: @Sendable (Avatar) async -> UIImage?
	public var preRender: @Sendable (Avatar) async -> Void

	public init(
		render: @escaping @Sendable (Avatar) async -> UIImage?,
		preRender: @escaping @Sendable (Avatar) async -> Void
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
	public var avatarService: AvatarService {
		get { self[AvatarService.self] }
		set { self[AvatarService.self] = newValue }
	}
}
