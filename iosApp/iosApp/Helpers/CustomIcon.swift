import SwiftUI

enum CustomIcon: String {
    case club = "ic_club"
    case clubs = "ic_clubs"
    case user = "ic_user"
    case book = "ic_book"
    case points = "ic_points"
    case location = "ic_location"
    case settings = "ic_settings"
    case help = "ic_help"
    case checkmark = "ic_checkmark"
    case logout = "ic_logout"
    case email = "ic_email"
    case password = "ic_password"

    var image: Image {
        Image(self.rawValue)
            .renderingMode(.template)
    }
}

extension Image {
    static func custom(_ icon: CustomIcon) -> Image {
        icon.image
    }
}
