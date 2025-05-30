import AchievementsLibrary
import AchievementsServiceInterface
import AnalyticsServiceInterface
import AppIconServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

@Reducer
public struct AppIconList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var availableAppIcons: Set<AppIcon> = []
		public var isLoadingAppIcon = true
		public var currentAppIcon: AppIcon?
		@Presents var alert: AlertState<Action.Alert>?
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didFirstAppear
			case didTapIcon(AppIcon)
			case didTapReset
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case didLoadAvailableAppIcons(Set<AppIcon>)
			case didUpdateIcon(Result<Never, Error>)
			case didFetchIcon(Result<AppIcon?, Error>)
			case alert(PresentationAction<Alert>)
		}
		public enum Alert: Equatable {}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(AchievementsService.self) var achievements
	@Dependency(AppIconService.self) var appIcon
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .merge(
						fetchCurrentAppIcon(),
						.run { send in await send(.internal(.didLoadAvailableAppIcons(appIcon.availableAppIcons()))) },
						.run { _ in await achievements.sendEvent(EarnableAchievements.Iconista.Events.AppIconsViewed(id: uuid())) }
					)

				case let .didTapIcon(icon):
					guard state.currentAppIcon != icon && !(icon == .primary && state.currentAppIcon == nil) else {
						return .none
					}

					return .concatenate(
						.run { _ in
							if icon == .primary {
								try await appIcon.resetAppIcon()
							} else {
								try await appIcon.setAppIcon(icon)
							}
						} catch: { error, send in
							await send(.internal(.didUpdateIcon(.failure(error))))
						},
						fetchCurrentAppIcon()
					)

				case .didTapReset:
					return .concatenate(
						.run { _ in
							try await appIcon.resetAppIcon()
						} catch: { error, send in
							await send(.internal(.didUpdateIcon(.failure(error))))
						},
						fetchCurrentAppIcon()
					)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadAvailableAppIcons(icons):
					state.availableAppIcons = icons
					return .none

				case let .didFetchIcon(.success(icon)):
					state.isLoadingAppIcon = false
					state.currentAppIcon = icon
					return .none

				case .didFetchIcon(.failure):
					state.alert = AlertState { TextState(Strings.Settings.AppIcon.List.Error.notFound) }
					return .none

				case .didUpdateIcon(.failure):
					state.alert = AlertState { TextState(Strings.Settings.AppIcon.List.Error.failedToChange) }
					return .none

				case .alert(.dismiss), .alert(.presented):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alert, action: \.internal.alert)

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .view(.didTapIcon(icon)):
				return Analytics.Settings.ChangedAppIcon(appIconName: icon.rawValue)
			case .view(.didTapReset):
				return Analytics.Settings.ChangedAppIcon(appIconName: AppIcon.primary.rawValue)
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didFetchIcon(.failure(error))),
				let .internal(.didUpdateIcon(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

@ViewAction(for: AppIconList.self)
public struct AppIconListView: View {
	@Bindable public var store: StoreOf<AppIconList>

	public var body: some View {
		List {
			Section {
				Button { send(.didTapReset) } label: {
					AppIconView(Strings.App.Icon.current, icon: .image(store.appIconImage))
				}
				.buttonStyle(.plain)
			}

			ForEach(AppIcon.Category.allCases) { category in
				Section(String(describing: category)) {
					ForEach(category.matchingIcons) { icon in
						if !store.availableAppIcons.contains(icon) {
							EmptyView()
						} else {
							Button { send(.didTapIcon(icon)) } label: {
								AppIconView(String(describing: icon), icon: .appIcon(icon))
							}
						}
					}
				}
			}
		}
		.navigationTitle(Strings.Settings.AppIcon.title)
		.onFirstAppear { send(.didFirstAppear) }
		.onAppear { send(.onAppear) }
		.alert($store.scope(state: \.alert, action: \.internal.alert))
	}
}

extension AppIcon.Category: CustomStringConvertible {
	public var description: String {
		switch self {
		case .pride: Strings.App.Icon.Category.pride
		case .standard: Strings.App.Icon.Category.standard
		case .christmas: Strings.App.Icon.Category.christmas
		case .halloween: Strings.App.Icon.Category.halloween
		}
	}
}

extension AppIcon: CustomStringConvertible {
	public var description: String {
		switch self {
		case .bisexual: Strings.App.Icon.bisexual
		case .candyCorn: Strings.App.Icon.candyCorn
		case .christmas: Strings.App.Icon.christmas
		case .dark: Strings.App.Icon.dark
		case .devilHorns: Strings.App.Icon.devilHorns
		case .fabric: Strings.App.Icon.fabric
		case .pride: Strings.App.Icon.pride
		case .primary: Strings.App.Icon.primary
		case .purple: Strings.App.Icon.purple
		case .trans: Strings.App.Icon.trans
		case .witchHat: Strings.App.Icon.witchHat
		}
	}
}

extension AppIcon {
	public var isProRequired: Bool {
		switch self {
		case .primary, .purple, .dark, .fabric,
				.bisexual, .pride, .trans,
				.christmas,
				.candyCorn, .devilHorns, .witchHat:
			false
		}
	}
}

// MARK: - Previews

#if DEBUG
#Preview {
	NavigationStack {
		AppIconListView(
			store: .init(
				initialState: .init(),
				reducer: { AppIconList() },
				withDependencies: {
					$0[AppIconService.self].getAppIconName = { @Sendable in nil }
				}
			)
		)
	}
}
#endif
