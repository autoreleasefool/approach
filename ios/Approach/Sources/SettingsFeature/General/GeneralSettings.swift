import AppIconServiceInterface
import AppInfoPackageServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct GeneralSettings: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var appIcon: Loadable<AppIcon, Never> = .notLoaded

		public var isShowingBugReportEmail: Bool = false
		public var isShowingSendFeedbackEmail: Bool = false

		public let appName: String
		public let appVersion: String

		public init() {
			@Dependency(\.appInfo) var appInfo
			self.appVersion = appInfo.getFullAppVersion()
			self.appName = Strings.App.name
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapAppIcon
		}
		@CasePathable
		public enum Internal {
			case didFetchIcon(Result<AppIcon?, Error>)
		}
		@CasePathable
		public enum Delegate {
			case openAppIconSettings
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Dependency(AppIconService.self) var appIcon

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return refreshAppIcon()

				case .didTapAppIcon:
					return .send(.delegate(.openAppIconSettings))
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didFetchIcon(.success(icon)):
					state.appIcon = .loaded(icon ?? .primary)
					return .none

				case .didFetchIcon(.failure):
					state.appIcon = .failed(nil, nil)
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func refreshAppIcon() -> Effect<Action> {
		.run { send in
			await send(.internal(.didFetchIcon(Result {
				AppIcon(rawValue: await appIcon.getAppIconName() ?? "")
			})))
		}
	}
}

// MARK: - View

@ViewAction(for: GeneralSettings.self)
public struct GeneralSettingsView: View {
	public let store: StoreOf<GeneralSettings>

	public var body: some View {
		List {
			if let appIcon = store.appIcon.value?.image {
				AppIconSection(
					appIconImage: appIcon,
					onTapAppIconButton: { send(.didTapAppIcon) }
				)
			}
		}
		.onAppear { send(.onAppear) }
	}
}
